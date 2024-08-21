import {
  Grid,
  Typography,
  CardContent,
  Card,
  CardActions,
  Button,
  Tooltip,
} from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import DatasetStyle from "../../../../styles/Dataset";
import { useEffect, useState } from "react";
import Start from "../../../../../assets/start.svg";
import Stop from "../../../../../assets/stopIcon.svg";
import InfoOutlinedIcon from "@material-ui/icons/InfoOutlined";
import AudioReactRecorder, { RecordState } from "audio-react-recorder";
import config from "../../../../../configs/configs";
// import StreamingClient from "../../../../../utils/streaming_client";
import {
  StreamingClient,
  SocketStatus,
} from "@project-sunbird/open-speech-streaming-client";
import { translate } from "../../../../../assets/localisation";
import LightTooltip from "../../../../components/common/LightTooltip";
import GetMasterDataAPI from "../../../../../redux/actions/api/Common/getMasterData";
import { useDispatch, useSelector } from "react-redux";
import APITransport from "../../../../../redux/actions/apitransport/apitransport";
import { useRef } from "react";

// const REACT_SOCKET_URL = config.REACT_SOCKET_URL;

const AudioRecord = (props) => {
  const streaming = props.streaming;
  const { classes, language, modelId, getchildData, feedback, inferenceEndPoint, setFeedbackAudioInput,task } = props;
  const [recordAudio, setRecordAudio] = useState("");
  const [streamingState, setStreamingState] = useState("");
  const [data, setData] = useState("");
  const { languages } = useSelector(
    (state) => state.getMasterData
  );

  const modelProcessingType = useSelector((state) => state.getModelDetails.inferenceEndPoint.schema.modelProcessingType?.type);
  const streamingEndPoint = inferenceEndPoint.callbackUrl
  //const streamingEndPoint = inferenceEndPoint.schema.request.audio[0].audioUri;

  
  const languageArr = languages.filter((lang) => lang.label === language);
  const languageCode = languageArr.length ? languageArr[0].code : "";
  const dispatch = useDispatch();
  const timerRef = useRef();
  const [base, setBase] = useState("");
  useEffect(() => {
    if (!languages.length) {
      const obj = new GetMasterDataAPI(["languages", "inferenceEndpoints"]);
      dispatch(APITransport(obj));
    }
  }, []);

  useEffect(() => {
    return () => {
      streaming.isStreaming ? streaming.disconnect() : console.log("unmounted");
      if (typeof timerRef.current === "number") clearTimeout(timerRef.current);
      timerRef.current = undefined;
    };
  }, []);

  useEffect(() => {
    if (streamingState === "start") {
      const output = document.getElementById("aldCardOutput");
      output.innerText = "";
      feedback.setTargetAudio("");
      feedback.setData("");
    }
  }, [streamingState]);

  const handleStart = (data) => {
    if (typeof timerRef.current === "number") {
      clearTimeout(timerRef.current);
    }

    if (streamingEndPoint) {
      setStreamingState("start");
      const output = document.getElementById("aldCardOutput");
      // output.innerText = "";

      setData("");
      streaming.connect(streamingEndPoint, languageCode, function (action, id) {
        timerRef.current = setTimeout(() => {
          if (streaming.isStreaming) handleStop();
        }, 61000);

        setStreamingState("listen");
        setRecordAudio(RecordState.START);

        if (action === SocketStatus.CONNECTED) {
          streaming.startStreaming(
            function (transcript) {
              const output = document.getElementById("aldCardOutput");
              if (output) output.innerText = transcript;
              getchildData(transcript);
            },

            function (errorMsg) {
              console.log("errorMsg", errorMsg);
            }
          );
        } else if (action === SocketStatus.TERMINATED) {
          setStreamingState("");
          streaming.stopStreaming((blob) => {});
          setRecordAudio(RecordState.STOP);
        } else {
          console.log("Action", action, id);
        }
      });
    } else {
      alert("Endpoint missing from master config");
    }
  };

  const handleStop = (value) => {
    setStreamingState("");
    const output = document.getElementById("aldCardOutput");
    if (output) {
      streaming.punctuateText(
        output.innerText,
        `${streamingEndPoint}asr/v1/punctuate/${languageCode}`,
        (status, text) => {
          output.innerText = text;
          getchildData(text);
        },
        (status, error) => {
          // alert("Failed to punctuate");
        }
      );
    }
    streaming.stopStreaming((blob) => {
      const urlBlob = window.URL.createObjectURL(blob);
      onStop({ url: urlBlob });
    });
    setRecordAudio(RecordState.STOP);
    if (typeof timerRef.current === "number") clearTimeout(timerRef.current);
  };

  const blobToBase64 = (blob) => {
    var reader = new FileReader();
    reader.readAsDataURL(blob.blob);
    reader.onloadend = function () {
      let base64data = reader.result;
      setBase(base64data);
      setFeedbackAudioInput(base64data);
    };
  };
  const onStop = (data) => {
    setData(data.url);
  };
  
  const handleCompute = () => {
    props.handleApicall(modelId, base, task , true);
  };

  const handleStartRecording = (data) => {
    setData(null);
    setRecordAudio(RecordState.START);
  };

  const handleStopRecording = (value) => {
    setRecordAudio(RecordState.STOP);
  };

  const onStopRecording = (data) => {
    setData(data.url);
    setBase(blobToBase64(data));
  };

  return (
    <Card className={classes.asrCard}>
      <Grid container className={classes.cardHeader}>
        <Typography variant="h6" className={classes.titleCard}>
          Hosted inference API{" "}
          {
            <LightTooltip
              arrow
              placement="right"
              title={translate("label.hostedInferenceASR")}
            >
              <InfoOutlinedIcon
                className={classes.buttonStyle}
                fontSize="small"
                color="disabled"
              />
            </LightTooltip>
          }
        </Typography>
      </Grid>
      {
      // props.submitter === "Vakyansh" ||
      // (props.submitter === "AI4Bharat" && version === "v3.0")
      modelProcessingType === "streaming"
       ? (
        <CardContent>
          <Typography variant={"caption"}>
            {translate("label.maxDuration")}
          </Typography>
          {recordAudio === "start" ? (
            <div className={classes.center}>
              <img
                src={Stop}
                alt=""
                onClick={() => handleStop()}
                style={{ cursor: "pointer" }}
              />{" "}
            </div>
          ) : (
            <div className={classes.center}>
              <img
                src={Start}
                alt=""
                onClick={() => handleStart()}
                style={{ cursor: "pointer" }}
              />{" "}
            </div>
          )}

          <div className={classes.center}>
            <Typography style={{ height: "12px" }} variant="caption">
              {streamingState === "start"
                ? "Please wait..."
                : streamingState === "listen"
                ? "Listening..."
                : ""}
            </Typography>{" "}
          </div>
          <div className={classes.centerAudio}>
            {data && <audio src={data} controls id="sample"></audio>}
          </div>
        </CardContent>
      ) : (
        <CardContent>
          {recordAudio === "start" ? (
            <div className={classes.center}>
              <img
                src={Stop}
                alt=""
                onClick={() => handleStopRecording()}
                style={{ cursor: "pointer" }}
              />{" "}
            </div>
          ) : (
            <div className={classes.center}>
              <img
                src={Start}
                alt=""
                onClick={() => handleStartRecording()}
                style={{ cursor: "pointer" }}
              />{" "}
            </div>
          )}
          <div className={classes.center}>
            <Typography style={{ height: "12px" }} variant="caption">
              {recordAudio === "start" ? "Recording..." : ""}
            </Typography>{" "}
          </div>
          <div style={{ display: "none" }}>
            <AudioReactRecorder
              state={recordAudio}
              onStop={onStopRecording}
              style={{ display: "none" }}
            />
          </div>
          <div className={classes.centerAudio}>
            {data ? (
              <audio src={data} controls id="sample"></audio>
            ) : (
              <audio src={"test"} controls id="sample"></audio>
            )}
          </div>

          <CardActions
            style={{ justifyContent: "flex-end", paddingRight: "20px" }}
          >
            <Button
              color="primary"
              variant="contained"
              size={"small"}
              disabled={data ? false : true}
              onClick={() => handleCompute()}
            >
              Convert
            </Button>
          </CardActions>
        </CardContent>
      )}
    </Card>
  );
};

export default withStyles(DatasetStyle)(AudioRecord);
