package com.api.regres

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.ResponseObject

import internal.GlobalVariable

public class getprofile {
	def APIStructureChecker = new com.api.structurecheck.ApiStructurecheck();
	@Keyword
	def verifyAPIGetProfile (ResponseObject response){
		KeywordUtil.logInfo("HEADER\n"+response.getHeaderFields()+"\n\nBODY\n"+response.getResponseBodyContent())
		JsonSlurper jsonSlurper = new JsonSlurper()
		def jsonResp = jsonSlurper.parseText(response.getResponseText())
		//API Name,File Location without Data Files, JSON Response Map, validation number(zero to skip validation)
		APIStructureChecker.StructureChecker('GetProfile','JSON/Get/GetProfile.txt', jsonResp, 1)
		KeywordUtil.logInfo("per_page " +jsonResp.per_page)
		if (jsonResp.total != null ) {
			for (int i = 0; i++; i<10){
				KeywordUtil.logInfo("data   " + jsonResp.data[i])
				if (jsonResp.data[i] == null){
					break ;
				}
				else {
					KeywordUtil.logInfo("data   " + jsonResp.data[i]	)
				}
			}
			KeywordUtil.logInfo("API Pass ")
		}
		else {
			KeywordUtil.markFailed("API Failed 1")
		}
	}

	@Keyword
	def verifyAPICreateProfile (ResponseObject response){
		KeywordUtil.logInfo("HEADER\n"+response.getHeaderFields()+"\n\nBODY\n"+response.getResponseBodyContent())
		JsonSlurper jsonSlurper = new JsonSlurper()
		def jsonResp = jsonSlurper.parseText(response.getResponseText())
		APIStructureChecker.StructureChecker('GetProfile','JSON/Get/CreateUser.txt', jsonResp, 1)
		KeywordUtil.logInfo("session id   " +jsonResp.id)
		if (jsonResp.id != null && jsonResp.name != null  && jsonResp.job != null) {
			KeywordUtil.logInfo("API Pass ")
		}
		else {
			KeywordUtil.markFailed("API Failed 1")
		}
	}
}
