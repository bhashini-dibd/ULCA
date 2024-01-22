package com.ulca.dataset.model.deserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import io.swagger.model.AsrParamsSchema.AgeEnum;
import io.swagger.model.AsrParamsSchema.DialectEnum;
import io.swagger.model.AudioBitsPerSample;
import io.swagger.model.AudioChannel;
import io.swagger.model.AudioFormat;
import io.swagger.model.AudioQualityEvaluation;
import io.swagger.model.AudioQualityEvaluation.MethodTypeEnum;
import io.swagger.model.CollectionDetailsAudioAutoAligned;
import io.swagger.model.CollectionDetailsMachineGeneratedTranscript;
import io.swagger.model.CollectionDetailsManualTranscribed;
import io.swagger.model.CollectionMethodAudio;
import io.swagger.model.DatasetType;
import io.swagger.model.Domain;
import io.swagger.model.DomainEnum;
import io.swagger.model.Gender;
import io.swagger.model.LanguagePair;
import io.swagger.model.MixedDataSource;
import io.swagger.model.Source;
import io.swagger.model.Submitter;
import io.swagger.model.SupportedLanguages;
import io.swagger.model.SupportedScripts;
import io.swagger.model.TranscriptionEvaluationMethod1;
import io.swagger.model.TtsParamsSchema;
import io.swagger.model.WadaSnr;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TtsParamsSchemaDeserializer extends StdDeserializer<TtsParamsSchema> {

	protected TtsParamsSchemaDeserializer(Class<?> vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}

	public TtsParamsSchemaDeserializer() {
		this(null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public TtsParamsSchema deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		log.info("******** Entry TtsParamsSchemaDeserializer :: deserialize ********");
		
		ObjectMapper mapper = new ObjectMapper();
		TtsParamsSchema ttsParamsSchema = new TtsParamsSchema();
		JsonNode node = p.readValueAsTree();

		ArrayList<String> errorList = new ArrayList<String>();
		JSONObject obj = new JSONObject(node.toPrettyString());

		Set<String> keys = obj.keySet();
		for (String k : keys) {
			try {
				/*
				 * TTS params schema is same as Asr params schema
				 */
				AsrDatasetParamsSchemaKeys key = AsrDatasetParamsSchemaKeys.valueOf(k); // tts params schema keys are same as asr param schema keys
			} catch (Exception ex) {
				log.info(k + " unknown property ");
				errorList.add(k + " unknown property ");
			}
		}

		// required
		if (!node.has("datasetType")) {
			errorList.add("datasetType field should be present");
		} else if (!node.get("datasetType").isTextual()) {
			errorList.add("datasetType field should be String");
		} else {
			String datasetType = node.get("datasetType").asText();
			DatasetType type = DatasetType.fromValue(datasetType);
			if (type != DatasetType.ASR_CORPUS) {
				errorList.add("datasetType field value " + DatasetType.ASR_CORPUS.toString());
			}
			ttsParamsSchema.setDatasetType(type);

		}
		if (!node.has("languages")) {
			errorList.add("languages field should be present");
		} else if (!node.get("languages").isObject()) {
			errorList.add("languages field should be JSON");
		} else {
			try {
				LanguagePair lp = new LanguagePair();

				if (node.get("languages").has("sourceLanguage")) {
					String sourceLanguage = node.get("languages").get("sourceLanguage").asText();
					if(SupportedLanguages.fromValue(sourceLanguage) != null) {
						lp.setSourceLanguage(SupportedLanguages.fromValue(sourceLanguage));
					} else {
						errorList.add("sourceLanguage is not one of defined language pair");
					}

				} else {
					errorList.add("sourceLanguage should be present");
				}
				if (node.get("languages").has("sourceLanguageName")) {
					String sourceLanguageName = node.get("languages").get("sourceLanguageName").asText();
					lp.setSourceLanguageName(sourceLanguageName);
				}
				if (node.get("languages").has("targetLanguage")) {
					String targetLanguage = node.get("languages").get("targetLanguage").asText();

					if(SupportedLanguages.fromValue(targetLanguage) != null) {
						lp.setTargetLanguage(SupportedLanguages.fromValue(targetLanguage));
					}

				}
				if (node.get("languages").has("targetLanguageName")) {
					String targetLanguageName = node.get("languages").get("targetLanguageName").asText();
					lp.setSourceLanguageName(targetLanguageName);
				}
				
				// Script code addition
				
				
				if (node.get("languages").has("sourceScriptCode")) {
					String sourceScriptCode = node.get("languages").get("sourceScriptCode").asText();
					if(SupportedScripts.fromValue(sourceScriptCode) != null) {
						lp.setSourceScriptCode(SupportedScripts.fromValue(sourceScriptCode));
					}else {
						errorList.add("sourceScriptCode is not one of defined language pair");
					}

				} 
			
				if (node.get("languages").has("targetScriptCode")) {
					String targetScriptCode = node.get("languages").get("targetScriptCode").asText();
					if(SupportedScripts.fromValue(targetScriptCode) != null) {
						lp.setTargetScriptCode(SupportedScripts.fromValue(targetScriptCode));
					}else {
						errorList.add("targetScriptCode is not one of defined language pair");
					}

				}
			

				ttsParamsSchema.setLanguages(lp);
			} catch (Exception e) {
				errorList.add("languages field value not proper.");
				e.printStackTrace();
			}

		}

		if (!node.has("collectionSource")) {
			errorList.add("collectionSource field should be present");
		} else if (!node.get("collectionSource").isArray()) {
			errorList.add("collectionSource field should be String array");
		} else {

			try {
				Source collectionSource = mapper.readValue(node.get("collectionSource").toPrettyString(), Source.class);
				if (collectionSource.size() > 10 || collectionSource.size() < 0) {
					errorList.add("collectionSource array size should be > 0 and <= 10");
				} else {
					ttsParamsSchema.setCollectionSource(collectionSource);
				}

			} catch (Exception e) {
				errorList.add("collectionSource field value not proper.");
				e.printStackTrace();
			}
		}

		if (!node.has("domain")) {
			errorList.add("domain field should be present");
		} else if (!node.get("domain").isArray()) {
			errorList.add("domain field should be String array");
		} else {

			try {
				Domain domain = new Domain();
				int size = node.get("domain").size();

				for (int i = 0; i < size; i++) {

					String enumValue = node.get("domain").get(i).asText();

					DomainEnum dEnum = DomainEnum.fromValue(enumValue);
					if (dEnum == null) {
						errorList.add("domain value not part of defined domains");
					} else {
						domain.add(enumValue);
					}
				}
				ttsParamsSchema.setDomain(domain);
			} catch (Exception e) {
				errorList.add("domain field value not proper.");
				e.printStackTrace();
			}
		}

		if (!node.has("license")) {
			errorList.add("license field should be present");
		} else if (!node.get("license").isTextual()) {
			errorList.add("license field should be String");
		} else {
			try {

				String licenseText = node.get("license").asText();

				io.swagger.model.License license = io.swagger.model.License.fromValue(licenseText);
				if (license != null) {
					ttsParamsSchema.setLicense(license);
					if(license == io.swagger.model.License.CUSTOM_LICENSE) {
						String licenseUrl = node.get("licenseUrl").asText();
						if(licenseUrl.isBlank()) {
							errorList.add("custom licenseUrl field value should be present");
						}
					}
				} else {
					errorList.add("license field value should be present in license list");
				}

			} catch (Exception e) {
				errorList.add("license field value not proper.");
				e.printStackTrace();
			}

		}

		if (node.get("submitter").isEmpty()) {
			errorList.add("submitter field should be present");
		} else if (!node.get("submitter").isObject()) {
			errorList.add("submitter field should be JSON");
		} else {
			try {
				Submitter submitter = mapper.readValue(node.get("submitter").toPrettyString(), Submitter.class);
				ttsParamsSchema.setSubmitter(submitter);
			} catch (Exception e) {
				errorList.add("submitter field value not proper.");
				e.printStackTrace();
			}
		}
		
		// optional params
		
				if (node.has("version")) {
					if (!node.get("version").isTextual()) {
						errorList.add("version field should be String");
					} else {
						String version = node.get("version").asText();
						ttsParamsSchema.setVersion(version);
					}

				}
				if (node.has("licenseUrl")) {
					if (!node.get("licenseUrl").isTextual()) {
						errorList.add("licenseUrl field should be String");
					} else {
						String licenseUrl = node.get("licenseUrl").asText();
						ttsParamsSchema.setLicenseUrl(licenseUrl);
					}

				}

				if (node.has("mixedDataSource")) {
					if (!node.get("mixedDataSource").isTextual()) {
						errorList.add("mixedDataSource field should be String");
					} else {
						String mixedDataSource = node.get("mixedDataSource").asText();
						MixedDataSource mixedDataSr = MixedDataSource.fromValue(mixedDataSource);
						if(mixedDataSr != null) {
							ttsParamsSchema.setMixedDataSource(mixedDataSr);
						}else {
							errorList.add("mixedDataSource not among one of specified values");
						}
					}
				}
				

		if (node.has("format")) {
			if (!node.get("format").isTextual()) {
				errorList.add("format field should be String");
			} else {
				String format = node.get("format").asText();
				AudioFormat audioFormat = AudioFormat.fromValue(format);
				if (audioFormat != null) {
					ttsParamsSchema.setFormat(audioFormat);
				} else {
					errorList.add("format not among one of specified");
				}
			}

		}

		if (node.has("channel")) {
			if (!node.get("channel").isTextual()) {
				errorList.add("channel field should be String");
			} else {
				String channel = node.get("channel").asText();
				AudioChannel audioChannel = AudioChannel.fromValue(channel);
				if (audioChannel != null) {
					ttsParamsSchema.setChannel(audioChannel);
				} else {
					errorList.add("channel not among one of specified");
				}

			}

		}

		if (node.has("samplingRate")) {
			if (!node.get("samplingRate").isNumber()) {
				errorList.add("samplingRate field should be Number");
			} else {
				BigDecimal samplingRate = node.get("samplingRate").decimalValue();
				ttsParamsSchema.setSamplingRate(samplingRate);
			}
		}

		if (node.has("bitsPerSample")) {
			if (!node.get("bitsPerSample").isTextual()) {
				errorList.add("bitsPerSample field should be String");
			} else {
				String bitsPerSample = node.get("bitsPerSample").asText();

				AudioBitsPerSample audioBitsPerSample = AudioBitsPerSample.fromValue(bitsPerSample);
				if (audioBitsPerSample != null) {
					ttsParamsSchema.setBitsPerSample(audioBitsPerSample);

				} else {
					errorList.add("bitsPerSample not among one of specified");
				}
			}

		}

		if (node.has("numberOfSpeakers")) {
			if (!node.get("numberOfSpeakers").isNumber()) {
				errorList.add("numberOfSpeakers field should be Number");
			} else {
				Integer numberOfSpeakers = node.get("numberOfSpeakers").asInt();
				if (numberOfSpeakers < 1) {
					errorList.add("numberOfSpeakers should be atleast 1");
				} else {
					ttsParamsSchema.setNumberOfSpeakers(new BigDecimal(numberOfSpeakers));
				}
			}
		}

		if (node.has("gender")) {
			if (!node.get("gender").isTextual()) {
				errorList.add("gender field should be String");
			} else {
				String gender = node.get("gender").asText();
				Gender genderenum = Gender.fromValue(gender);
				if (genderenum != null) {
					ttsParamsSchema.setGender(genderenum);
				} else {
					errorList.add("gender not among one of specified values");
				}
			}
		}

		if (node.has("age")) {
			if (!node.get("age").isTextual()) {
				errorList.add("age field should be String");
			} else {
				String age = node.get("age").asText();
				AgeEnum ageEnum = AgeEnum.fromValue(age);
				if (ageEnum != null) {
					ttsParamsSchema.setAge(ageEnum);
				} else {
					errorList.add("age not among one of specified values");
				}
			}
		}
		if (node.has("dialect")) {
			if (!node.get("dialect").isTextual()) {
				errorList.add("dialect field should be String");
			} else {
				String dialect = node.get("dialect").asText();
				DialectEnum dialectEnum = DialectEnum.fromValue(dialect);
				if (dialectEnum != null) {
					ttsParamsSchema.setDialect(dialectEnum);
				} else {
					errorList.add("dialect not among one of specified values");
				}
			}
		}

		if (node.has("snr")) {
			if (!node.get("snr").has("methodType")) {
				errorList.add("methodType should be present");
			} else if (!node.get("snr").get("methodType").isTextual()) {
				errorList.add("methodType should be String");
			} else {
				String methodType = node.get("snr").get("methodType").asText();
				if (methodType.equals("WadaSnr")) {
					WadaSnr wadaSnr = mapper.readValue(node.get("snr").get("methodDetails").toPrettyString(),
							WadaSnr.class);
					AudioQualityEvaluation audioQualityEvaluation = new AudioQualityEvaluation();
					audioQualityEvaluation.setMethodType(MethodTypeEnum.WADASNR);
					audioQualityEvaluation.setMethodDetails(wadaSnr);
					ttsParamsSchema.setSnr(audioQualityEvaluation);

				} else {
					errorList.add("methodType is not one of specified values");
				}
			}
		}

		if (node.has("collectionMethod")) {
			if (node.get("collectionMethod").has("collectionDescription")) {
				if (!node.get("collectionMethod").get("collectionDescription").isArray()) {
					errorList.add("collectionDescription field should be String Array");
				} else {

					try {
						String collectionDescription = node.get("collectionMethod").get("collectionDescription").get(0)
								.asText();
						CollectionMethodAudio.CollectionDescriptionEnum collectionDescriptionEnum = CollectionMethodAudio.CollectionDescriptionEnum
								.fromValue(collectionDescription);
						
						CollectionMethodAudio collectionMethodAudio = new CollectionMethodAudio();
						List<CollectionMethodAudio.CollectionDescriptionEnum> list = new ArrayList<CollectionMethodAudio.CollectionDescriptionEnum>();
						list.add(collectionDescriptionEnum);
						collectionMethodAudio.setCollectionDescription(list);
						ttsParamsSchema.setCollectionMethod(collectionMethodAudio);

						/*
						 * collectionDetails is non mandatory
						 */
						if (node.get("collectionMethod").has("collectionDetails")) { 
							
						switch (collectionDescriptionEnum) {
						case AUTO_ALIGNED:
							if (node.get("collectionMethod").get("collectionDetails").has("alignmentTool")) {
								if (node.get("collectionMethod").get("collectionDetails").get("alignmentTool")
										.isTextual()) {
									String alignmentTool = node.get("collectionMethod").get("collectionDetails")
											.get("alignmentTool").asText();
									CollectionDetailsAudioAutoAligned.AlignmentToolEnum alignmentToolEnum = CollectionDetailsAudioAutoAligned.AlignmentToolEnum
											.fromValue(alignmentTool);
									if (alignmentToolEnum != null) {

										CollectionDetailsAudioAutoAligned collectionDetailsAudioAutoAligned = mapper
												.readValue(
														node.get("collectionMethod").get("collectionDetails")
																.toPrettyString(),
														CollectionDetailsAudioAutoAligned.class);

										collectionMethodAudio.setCollectionDetails(collectionDetailsAudioAutoAligned);
										ttsParamsSchema.setCollectionMethod(collectionMethodAudio);

									} else {
										errorList.add("alignmentTool should be from one of specified values");
									}

								} else {
									errorList.add("alignmentTool should be String");
								}

							} else {
								errorList.add("alignmentTool should be present");
							}

							break;

						case MACHINE_GENERATED_TRANSCRIPT:

							CollectionDetailsMachineGeneratedTranscript collectionDetailsMachineGeneratedTranscript = new CollectionDetailsMachineGeneratedTranscript();

							TranscriptionEvaluationMethod1 transcriptionEvaluationMethod1 = mapper
									.readValue(
											node.get("collectionMethod").get("collectionDetails")
													.get("evaluationMethod").toPrettyString(),
											TranscriptionEvaluationMethod1.class);

							collectionDetailsMachineGeneratedTranscript
									.setEvaluationMethod(transcriptionEvaluationMethod1);

							if (node.get("collectionMethod").get("collectionDetails").has("asrModel")) {
								collectionDetailsMachineGeneratedTranscript.setAsrModel(
										node.get("collectionMethod").get("collectionDetails").get("asrModel").asText());
								collectionMethodAudio.setCollectionDetails(collectionDetailsMachineGeneratedTranscript);
							}

							if (node.get("collectionMethod").get("collectionDetails").has("evaluationMethodType")) {
								String evaluationMethodType = node.get("collectionMethod").get("collectionDetails")
										.get("evaluationMethodType").asText();

								CollectionDetailsMachineGeneratedTranscript.EvaluationMethodTypeEnum evaluationMethodTypeEnum = CollectionDetailsMachineGeneratedTranscript.EvaluationMethodTypeEnum
										.fromValue(evaluationMethodType);
								collectionDetailsMachineGeneratedTranscript
										.setEvaluationMethodType(evaluationMethodTypeEnum);

							}
							if (node.get("collectionMethod").get("collectionDetails").has("asrModelVersion")) {
								collectionDetailsMachineGeneratedTranscript
										.setAsrModelVersion(node.get("collectionMethod").get("collectionDetails")
												.get("asrModelVersion").asText());

							}
							ttsParamsSchema.setCollectionMethod(collectionMethodAudio);
							log.info("machine-generated-transcript");
							break;

						case MANUAL_TRANSCRIBED:

							CollectionDetailsManualTranscribed collectionDetailsManualTranscribed = mapper.readValue(
									node.get("collectionMethod").get("collectionDetails").toPrettyString(),
									CollectionDetailsManualTranscribed.class);
							collectionMethodAudio.setCollectionDetails(collectionDetailsManualTranscribed);
							ttsParamsSchema.setCollectionMethod(collectionMethodAudio);
							log.info("manual-transcribed");
							break;
						}
					}
					} catch (Exception e) {
						log.info("collection method not proper");
						errorList.add("collectionMethod field value not proper.");
						log.info("tracing the error");
						e.printStackTrace();
					}
				}
			}
		}

		if (!errorList.isEmpty())
			throw new IOException(errorList.toString());

		log.info("******** Exiting TtsParamsSchemaDeserializer :: deserializer ********");
		return ttsParamsSchema;
	}

}
