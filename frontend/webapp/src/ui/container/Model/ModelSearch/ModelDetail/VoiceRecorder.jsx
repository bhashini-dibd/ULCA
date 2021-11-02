import { Grid, Typography, CardContent, Card } from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import DatasetStyle from "../../../../styles/Dataset";
import { useState } from "react";
import Start from "../../../../../assets/start.svg";
import Stop from "../../../../../assets/stopIcon.svg";
import InfoOutlinedIcon from "@material-ui/icons/InfoOutlined";
import { RecordState } from "audio-react-recorder";
import config from "../../../../../configs/configs";
// import StreamingClient from "../../../../../utils/streaming_client";
import {
  StreamingClient,
  SocketStatus,
} from "@project-sunbird/open-speech-streaming-client";
import { vakyanshLanguage } from "../../../../../configs/DatasetItems";
const SOCKET_URL = config.SOCKET_URL;

const AudioRecord = (props) => {
  const [streaming, setStreaming] = useState(new StreamingClient());
  const { classes, language } = props;
  const [recordAudio, setRecordAudio] = useState("");
  const [streamingState, setStreamingState] = useState("");
  const [data, setData] = useState("");
  const languageArr = vakyanshLanguage.filter(
    (lang) => lang.label === language
  );
  const languageCode = languageArr.length ? languageArr[0].value : "";
  const handleStart = (data) => {
    setStreamingState("start");
    const output = document.getElementById("asrCardOutput");
    output.innerText = "";
    setData(null);
    streaming.connect(SOCKET_URL, languageCode, function (action, id) {
      setStreamingState("listen");
      setRecordAudio(RecordState.START);
      setTimeout(() => {
        setRecordAudio(RecordState.STOP);
        setStreamingState("");
      }, 61000);
      if (action === SocketStatus.CONNECTED) {
        streaming.startStreaming(
          function (transcript) {
            const output = document.getElementById("asrCardOutput");
            if (output) output.innerText = transcript;
          },
          function (errorMsg) {
            console.log("errorMsg", errorMsg);
          }
        );
      } else if (action === SocketStatus.TERMINATED) {
        handleStop();
      } else {
        console.log("Action", action, id);
      }
    });
  };

  const handleStop = (value) => {
    setStreamingState("");
    const output = document.getElementById("asrCardOutput");
    streaming.punctuateText(
      output.innerText,
      "https://inference.vakyansh.in/punctuate",
      (status, text) => {
        output.innerText = text;
      },
      (status, error) => {
        // alert("Failed to punctuate");
      }
    );
    streaming.stopStreaming((blob) => {
      const urlBlob = window.URL.createObjectURL(blob);
      onStop({ url: urlBlob });
    });
    setRecordAudio(RecordState.STOP);
  };

  const onStop = (data) => {
    setData(data.url);
  };

  return (
    <Card className={classes.asrCard}>
      <Grid container className={classes.cardHeader}>
        <Typography variant="h6" className={classes.titleCard}>
          Hosted inference API{" "}
          {
            <InfoOutlinedIcon
              className={classes.buttonStyle}
              fontSize="small"
              color="disabled"
            />
          }
        </Typography>
      </Grid>
      <CardContent>
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
    </Card>
  );
};

export default withStyles(DatasetStyle)(AudioRecord);
