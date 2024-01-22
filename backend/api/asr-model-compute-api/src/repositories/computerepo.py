from logging import exception
import traceback
import os
from pydub import AudioSegment
from models.response import CustomResponse, post_error
import base64
import json
from config import shared_storage_path,secret_key
import base64
import requests
import logging
from logging.config import dictConfig
log = logging.getLogger('file')
from Crypto.Cipher import AES
import base64
import sys
import os

class ASRComputeRepo:

    def decrypt(self, key, message):
        """
        Input encrypted bytes, return decrypted bytes, using iv and key
        """

        byte_array = base64.b64decode(message)

        iv = byte_array[0:16] # extract the 16-byte initialization vector

        messagebytes = byte_array[16:] # encrypted message is the bit after the iv

        cipher = AES.new(key.encode("UTF-8"), AES.MODE_CBC, iv )

        decrypted_padded = cipher.decrypt(messagebytes)

        last_byte = decrypted_padded[-1]

        decrypted = decrypted_padded[0:-last_byte]

        return decrypted.decode("UTF-8")


    def process_asr(self,lang,audio,userId,inf_callbackurl,uri):
        """
        Processing audio urls / encoded audio content
        If url, directly initiating the model call
        If audio is base64 encoded :
        - Decoding back from base64
        - setting channel to 1
        - Setting frame rate to 16k
        - Encoding back to base64
        - decoding to utf-8
        """
        
        callbackurl =   inf_callbackurl["callbackUrl"]
        if "inferenceApiKey" in inf_callbackurl.keys():
            if "name" in inf_callbackurl["inferenceApiKey"].keys() and "value" in inf_callbackurl["inferenceApiKey"].keys():
                apiKeyName = self.decrypt(secret_key,inf_callbackurl["inferenceApiKey"]["name"])
                apiKeyValue = self.decrypt(secret_key,inf_callbackurl["inferenceApiKey"]["value"])
               
                
            elif  "name" not in inf_callbackurl["inferenceApiKey"].keys() and "value" in inf_callbackurl["inferenceApiKey"].keys():
                apiKeyName = None
                apiKeyValue = self.decrypt(secret_key,inf_callbackurl["inferenceApiKey"]["value"])
        else:
            apiKeyName = None
            apiKeyValue = None
    

        transformat =   inf_callbackurl["schema"]["request"]["config"]["transcriptionFormat"]["value"].lower()
        audioformat =   inf_callbackurl["schema"]["request"]["config"]["audioFormat"].lower()
        log.info(f'callbackurl == {callbackurl}, transformat=={transformat}, audioformat=={audioformat}')
        if uri == True:
            result = self.make_audiouri_call(audio,lang,callbackurl,transformat,audioformat,apiKeyName,apiKeyValue)
            return result
        else:
            try:
                # callbackurl =   inf_callbackurl["callbackUrl"]
                # if "name" in inf_callbackurl["inferenceApiKey"].keys() and "value" in inf_callbackurl["inferenceApiKey"].keys():
                #     apiKeyName = inf_callbackurl["inferenceApiKey"]["name"]
                #     apiKeyValue = inf_callbackurl["inferenceApiKey"]["value"]
                # elif  "name" not in inf_callbackurl["inferenceApiKey"].keys() and "value" in inf_callbackurl["inferenceApiKey"].keys():
                #     apiKeyName = None
                #     apiKeyValue = inf_callbackurl["inferenceApiKey"]["value"]
                # else:
                #     apiKeyName = None
                #     apiKeyValue = None
                encode_string = audio
                file = f'{shared_storage_path}audio-{userId}.wav'
                with open (file, "wb") as wav_file:
                    decode_string = base64.b64decode(encode_string)
                    wav_file.write(decode_string)

                audio = AudioSegment.from_file(file)
                audio = audio.set_channels(1)
                audio = audio.set_frame_rate(16000)
                processed_file = f'{shared_storage_path}audio-{userId}-processed.wav'
                audio.export(processed_file, format="wav")

                encoded_data=base64.b64encode(open(processed_file, "rb").read()) 
                #log.info(f'encoded data {encoded_data}')
                os.remove(file)
                os.remove(processed_file)
                
                result = self.make_base64_audio_processor_call(encoded_data.decode("utf-8"),lang,callbackurl,transformat,audioformat,apiKeyName,apiKeyValue,punctiation=True)
                log.info(f"result make_base64_audio_processor_call {result}")
                return result

            except Exception as e:
                log.info(f'Exception while processing request: {e}')
                log.info(f'Traceback of exception: {traceback.print_exc()}')
                return []

    def process_asr_from_audio_file(self,lang,audio_file_path,callback_url,transformat,audioformat,punctiation=False):
        """
        Processing audio files.
        - Reading file from shared storage
        - setting channel to 1
        - Setting frame rate to 16k
        - Encoding back to base64
        - decoding to utf-8
        """
        try:
            audio = AudioSegment.from_file(audio_file_path)
            audio = audio.set_channels(1)
            audio = audio.set_frame_rate(16000)
            processed_file = f'{shared_storage_path}audio-processed.wav'
            audio.export(processed_file, format="wav")
            encoded_data=base64.b64encode(open(processed_file, "rb").read()) 
            os.remove(processed_file)
            result = self.make_base64_audio_processor_call(encoded_data.decode("utf-8"),lang,callback_url,transformat,audioformat,apiKeyName="NONE",apiKeyValue="NONE",punctiation=True)
            return result

        except Exception as e:
            log.info(f'Exception while processing request: {e}')
            return {}


    
    def make_audiouri_call(self, url,lang,callbackurl,transformat,audioformat,apiKeyName,apiKeyValue):
        """
        API call to model endpoint for audio urls
        """
        body    =   {"config": {"language": {"sourceLanguage": lang},"transcriptionFormat": {"value":transformat},"audioFormat": audioformat},
                        "audio": [{"audioUri": url}]}
        log.info(f"Request body : {body}")
        try:
            if apiKeyName and apiKeyValue:
                headers =   {"Content-Type": "application/json", apiKeyName: apiKeyValue }
            elif apiKeyValue and apiKeyName == None:
                apiKeyName = apiKeyValue
                headers =   {"Content-Type": "application/json", "apiKey":apiKeyValue}
            elif apiKeyValue == None and apiKeyName == None:
                headers =   {"Content-Type": "application/json"}
            
            
            #headers =   {"Content-Type": "application/json"}
            body    =   {"config": {"language": {"sourceLanguage": lang},"transcriptionFormat": {"value":transformat},"audioFormat": audioformat},
                        "audio": [{"audioUri": url}]}
            log.info(f"Request body : {body}")
            request_url = callbackurl
            log.info("Intiating request to process asr data on %s"%request_url)
            response = requests.post(url=request_url, headers = headers, json = body)
            content = response.content
            response_data = json.loads(content)
            return response_data
        except Exception as e:
            log.exception(f'Exception while making api call: {e}')
            return {"status_text":"Incorrect inference endpoint or invalid response"}


    def make_base64_audio_processor_call(self,data,lang,callbackurl,transformat,audioformat,apiKeyName,apiKeyValue,punctiation):
        """
        API call to model endpoint for audio content
        """
        try:
            if apiKeyName == "NONE" and apiKeyValue == "NONE":
                headers =   {"Content-Type": "application/json"}
            elif apiKeyName and apiKeyValue:
                headers =   {"Content-Type": "application/json", apiKeyName: apiKeyValue }
            elif apiKeyValue and apiKeyName == None:
                apiKeyName = apiKeyValue
                headers =   {"Content-Type": "application/json", "apiKey":apiKeyValue}
            elif apiKeyValue == None and apiKeyName == None:
                headers =   {"Content-Type": "application/json"}
            body    =   {"config": {"language": {"sourceLanguage": lang},"transcriptionFormat": {"value":transformat},"audioFormat": audioformat,
                        "punctuation": punctiation,"enableInverseTextNormalization": False},"audio": [{"audioContent": str(data)}]}
            request_url = callbackurl
            #log.info(f'transformat == > {transformat}, audioformat==> {audioformat}, lang ==> {lang} punc ==> {punctiation}, audioContent ==> {data}')
            log.info(f"logging request body {body}")
            log.info("Intiating request to process asr data on %s"%request_url)
            response = requests.post(url=request_url, headers = headers, json = body,verify=False)
            #return response instead of response_data
            content = response.content
            log.info(content) 
            response_data = json.loads(content)
            log.info("Received response from inference end point to transcribe asr data")
            log.info(f"Response : {response_data}")
            return response_data
        except Exception as e:
            log.exception(f'Exception while making api call: {e}')
            return {"status_text":"Incorrect inference endpoint or invalid response"}



dictConfig({
    'version': 1,
    'formatters': {'default': {
        'format': '[%(asctime)s] {%(filename)s:%(lineno)d} %(threadName)s %(levelname)s in %(module)s: %(message)s',
    }},
    'handlers': {
        'info': {
            'class': 'logging.FileHandler',
            'level': 'DEBUG',
            'formatter': 'default',
            'filename': 'info.log'
        },
        'console': {
            'class': 'logging.StreamHandler',
            'level': 'DEBUG',
            'formatter': 'default',
            'stream': 'ext://sys.stdout',
        }
    },
    'loggers': {
        'file': {
            'level': 'DEBUG',
            'handlers': ['info', 'console'],
            'propagate': ''
        }
    },
    'root': {
        'level': 'DEBUG',
        'handlers': ['info', 'console']
    }
})