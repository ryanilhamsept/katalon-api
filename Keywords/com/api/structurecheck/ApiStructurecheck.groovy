package com.api.structurecheck

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
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.apache.commons.io.FileUtils as FileUtils
import internal.GlobalVariable
import groovy.json.JsonSlurper as JsonSlurper
import com.kms.katalon.core.util.KeywordUtil


public class ApiStructurecheck {
	def FileReader (String FileLocation){

		File file = new File(FileLocation)
		String text = FileUtils.readFileToString(file)
		JsonSlurper jsonSlurper = new JsonSlurper()
		def jsonResp = jsonSlurper.parseText(text)
		return jsonResp
	}
	def GetJSONKey (Map JSONText){

		def traverse
		traverse = { def tree, def keys = [], def prefix = '' ->
			switch (tree) {
				case Map:
					tree.each({ def k, def v ->
						def name = prefix ? "$prefix.$k" : k
						keys << name
						traverse(v, keys, name)
					})
					return keys
				case Collection:
					tree.eachWithIndex({ def e, def i ->
						traverse(e, keys, "$prefix")
					})
					return keys
				default:
					return keys
			}
		}
		List<String> result = []
		traverse(JSONText).each({
			if(!result.contains(it.toString())) {
				result.add(it.toString())
			}
		})
		Collections.sort(result)
		return result
	}

	def verifyResult (List<String> key_from_file, List<String> key_from_response,int code){
		switch(code){
			case 1:
				if (key_from_file.equals(key_from_response)) {
					KeywordUtil.logInfo("API Structure Check Validation 1 Pass")
				}else {
					String missing_key_file=''
					for(int i in 0..key_from_file.size()-1) {
						if(!key_from_response.contains(key_from_file.get(i))) {
							if(missing_key_file.equals('')) {
								missing_key_file = key_from_file.get(i)
							}else {
								missing_key_file = missing_key_file+', '+key_from_file.get(i)
							}
						}
					}
					String missing_key_response=''
					for(int i in 0..key_from_response.size()-1) {
						if(!key_from_file.contains(key_from_response.get(i))) {
							if(missing_key_response.equals('')) {
								missing_key_response = key_from_response.get(i)
							}else {
								missing_key_response = missing_key_response+', '+key_from_response.get(i)
							}
						}
					}
					String report=''
					if(missing_key_file.equals('')) {
						report='Missing Key from Response : '+missing_key_response
					}else if(missing_key_response.equals('')) {
						report='Missing Key from File : '+missing_key_file
					}else {
						report='Missing Key from File : '+missing_key_file+'\nMissing Key from Response : '+missing_key_response
					}
					KeywordUtil.markFailed("API Structure Check Validation 1 Failed\n"+report)
				}
				break;
			case 2:		//all structure in file is available on response
				boolean status=true;
				String missing_key=''
				for(int i in 0..key_from_file.size()-1) {
					if(!key_from_response.contains(key_from_file.get(i))) {
						status=false
						if(missing_key.equals('')) {
							missing_key = key_from_file.get(i)
						}else {
							missing_key = missing_key+', '+key_from_file.get(i)
						}
					}
				}
				status ? KeywordUtil.logInfo("API Structure Check Validation 2 Pass") : KeywordUtil.markFailed("API Structure Check Validation 2 Failed, missing keys from response :"+missing_key)
				break;
			case 3:		//all structure in response is available on file
				boolean status=true;
				String missing_key=''
				for(int i in 0..key_from_response.size()-1) {
					if(!key_from_file.contains(key_from_response.get(i))) {
						status=false
						if(missing_key.equals('')) {
							missing_key = key_from_response.get(i)
						}else {
							missing_key = missing_key+', '+key_from_response.get(i)
						}
					}
				}
				status ? KeywordUtil.logInfo("API Structure Check Validation 3 Pass") : KeywordUtil.markFailed("API Structure Check Validation 3 Failed, additional keys from response :"+missing_key)
				break;
			default:
				KeywordUtil.logInfo("API Structure Checker ignored!!!")
		}

	}

	def StructureChecker (String APIName, String FileLocation, Map JSONText, int code){
		if (code != 0) {
			KeywordUtil.logInfo("Starting JSON Structure Checker on $APIName")
			def key_from_file=this.GetJSONKey(this.FileReader(FileLocation))
			KeywordUtil.logInfo("key_from_file : "+key_from_file)
			def key_from_response=this.GetJSONKey(JSONText)
			KeywordUtil.logInfo("key_from_response : "+key_from_response)
			verifyResult (key_from_file, key_from_response, code)
		}else {
			KeywordUtil.logInfo("API Structure Checker on $APIName ignored!!!")
		}
	}
}

