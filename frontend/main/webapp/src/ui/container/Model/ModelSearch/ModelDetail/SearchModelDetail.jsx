import { withStyles } from "@material-ui/core/styles";
import DatasetStyle from "../../../../styles/Dataset";
import { ArrowBack } from "@material-ui/icons";
import { useHistory, useParams } from "react-router";
import ModelDescription from "./ModelDescription";
import HostedInference from "./HostedInference";
import { useLocation } from "react-router-dom";
import React, { useEffect, useState } from "react";
import Header from "../../../../components/common/Header";
import Footer from "../../../../components/common/Footer";
import Theme from "../../../../theme/theme-default";
import { MuiThemeProvider } from "@material-ui/core/styles";
import {
  Grid,
  Typography,
  Button,
  Tabs,
  Tab,
  AppBar,
  Box,
  Card,
  Tooltip,
} from "@material-ui/core";
import HostedInferASR from "./HostedInferASR";
import HostedInferOCR from "./HostedInferOCR";
import HostedInferTTS from "./HostedInferenceTTS";
import BenchmarkTable from "./BenchmarkTable";
import { translate } from "../../../../../assets/localisation";
import { StreamingClient } from "@project-sunbird/open-speech-streaming-client";
import GetModelDetails from "../../../../../redux/actions/api/Model/ModelSearch/GetModelDetails";
import APITransport from "../../../../../redux/actions/apitransport/apitransport";
import { useDispatch, useSelector } from "react-redux";
import PropTypes from "prop-types";
import HostedInferTransliteration from "./HostedInferTransliteration";
import LanugageDetection from "../LanugageDetection";
import metricInfo from "../../../../../utils/getMetricInfo.";
import HostedinferenceNER from "./HostedinferenceNER";
import Contactus from "../../../../components/common/Contactus";
import Clients from "../../../../components/common/Clients";
import { FooterNewDesign } from "../../../../components/common/FooterNewDesign";
import HostedInferALD from "./HostedInferALD";
import HostedInferAGD from "./HostedInferAGD";
import HostedInferenceITN from "./HostedInferenceITN";
import HostedInferenceTN from "./HostedInferenceTN";

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`full-width-tabpanel-${index}`}
      aria-labelledby={`full-width-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

const SearchModelDetail = (props) => {
  const { classes } = props;
  const history = useHistory();
  const [value, setValue] = React.useState(0);
  const [index, setIndex] = useState(0);
  const [metric, setMetric] = useState("");
  const [modelTry, setModelTry] = useState(false);
  const [streaming, setStreaming] = useState(new StreamingClient());
  const location = useLocation();
  const params = useParams();
  const dispatch = useDispatch();
  const data = useSelector((state) => state.getModelDetails);
  const {
    modelName,
    task,
    source,
    language,
    inferenceEndPoint,
    submitter,
    target,
    benchmarkPerformance,
    metricArray,
  } = useSelector((state) => state.getModelDetails);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  useEffect(() => {
    const obj = new GetModelDetails(params.srno);
    dispatch(APITransport(obj));
  }, []);

  const description = data.result;
  console.log("description ------- ", description);

  const [prevUrl, setUrl] = useState(
    location.state ? location.state.prevUrl : "explore-models"
  );
  const handleCardNavigation = () => {
    if (task === "asr" && streaming.isStreaming === true) {
      streaming.stopStreaming((blob) => {
        clearTimeout();
      });
    }
    // const { prevUrl } = location.state
    if (prevUrl === "explore-models") {
      history.push(`${process.env.PUBLIC_URL}/model/explore-models`);
    } else {
      history.push(`${process.env.PUBLIC_URL}/model/explore-models`);
    }
  };

  const handleClick = () => {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search-model/${params.srno}/model`,
      state: data,
    });
  };

  const handleIndexChange = (metric) => {
    setIndex(metricArray.indexOf(metric));
    setMetric(metric);
  };

  const renderHostedInfer = (task) => { 
    if (data) {
      switch (task) {
        case "asr":
          return (
            <HostedInferASR
              task={task}
              source={source}
              language={language}
              inferenceEndPoint={inferenceEndPoint}
              submitter={submitter}
              modelId={params.srno}
              streaming={streaming}
            />
          );
        case "ocr":
          return (
            <HostedInferOCR
              task={task}
              source={source}
              inferenceEndPoint={inferenceEndPoint}
              modelId={params.srno}
            />
          );
        case "tts":
          return (
            <HostedInferTTS
              task={task}
              source={source}
              inferenceEndPoint={inferenceEndPoint}
              modelId={params.srno}
              submitter={submitter}
            />
          );
        case "ner":
          return (
            <HostedinferenceNER
              task={task}
              source={source}
              inferenceEndPoint={inferenceEndPoint}
              modelId={params.srno}
              submitter={submitter}
            />
          );
        case "transliteration":
          return (
            <HostedInferTransliteration
              task={task}
              modelId={params.srno}
              source={source}
              target={target}
            />
          );
        case "txt-lang-detection":
          return (
            <LanugageDetection
              task={task}
              modelId={params.srno}
              source={source}
              target={target}
            />
          );
        case "audio-lang-detection":
          return (
          <HostedInferALD
              task={task}
              source={source}
              language={language}
              inferenceEndPoint={inferenceEndPoint}
              submitter={submitter}
              modelId={params.srno}
              streaming={streaming}
            />
          );
        case "audio-gender-detection":
            return (
            <HostedInferAGD
                task={task}
                source={source}
                language={language}
                inferenceEndPoint={inferenceEndPoint}
                submitter={submitter}
                modelId={params.srno}
                streaming={streaming}
              />
            );
           case "itn":
              return (
                <HostedInferenceITN
                task={task}
                modelId={params.srno}
                source={source}
                target={target}
              />
              );
              case "text-normalization":
              return (
                <HostedInferenceTN
                task={task}
                modelId={params.srno}
                source={source}
                target={target}
              />
              );
        default:
          return (
            <HostedInference
              task={task}
              modelId={params.srno}
              source={source}
              target={target}
            />
          );
      }
    }
  };

  return (
    <MuiThemeProvider theme={Theme}>
      <Header style={{ marginBottom: "10px" }} />
      {data && (
        <div className={classes.parentPaper}>
          <Button
            size="small"
            color="primary"
            className={classes.backButton}
            startIcon={<ArrowBack />}
            onClick={() => handleCardNavigation()}
          >
            {prevUrl === "explore-models"
              ? translate("label.backToModelList")
              : translate("label.backToModelList")}
          </Button>

          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <Grid container>
              <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                <Card className={classes.modelNameCard}>
                  <Grid container>
                    <Grid item xs={10} sm={10} md={10} lg={10} xl={10}>
                      <Typography
                        variant="h4"
                        color="secondary"
                        className={classes.mainTitle}
                      >
                        {modelName}
                      </Typography>
                    </Grid>
                    {!params.model && (
                      <Grid item xs={2} sm={2} md={2} lg={2} xl={2}>
                        <Button
                          color="primary"
                          className={classes.computeBtn}
                          variant="contained"
                          size={"small"}
                          onClick={() => handleClick()}
                        >
                          {translate("label.tryModel")}
                        </Button>
                      </Grid>
                    )}
                  </Grid>
                </Card>
              </Grid>
            </Grid>
          </div>
          {/* <hr style={{marginTop: "19px",opacity:'0.3' }}></hr> */}
          {/* <Divider className={classes.gridCompute} /> */}
          {params.model ? (
            <Grid container>
              <Grid
                className={classes.leftSection}
                item
                xs={12}
                sm={12}
                md={8}
                lg={8}
                xl={8}
              >
                {renderHostedInfer(task)}
              </Grid>
              <Grid
                item
                xs={12}
                sm={12}
                md={4}
                lg={4}
                xl={4}
                className={classes.rightSection}
              // style={{ paddingLeft: "24px" }}
              >
                <Grid container spacing={2} style={{ marginTop: "2%" }}>
                  {/* <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                    <Typography variant="h6" className={classes.modelTitle}>Version</Typography>
                    <Typography style={{ fontSize: '20px', fontFamily: 'Roboto', textAlign: "justify" }} className={classes.modelPara}>{data.version}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                    <Typography variant="h6" className={classes.modelTitle}>Description</Typography>
                    <Typography style={{ fontSize: '20px', fontFamily: 'Roboto', textAlign: "justify" }} className={classes.modelPara}>{data.description}</Typography>

                  </Grid> */}
                  <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                    <Grid container spacing={1}>
                      {description?.map((des, i) => (
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                          <ModelDescription
                            title={des.title}
                            para={des.para}
                            index={i}
                          />
                        </Grid>
                      ))}
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          ) : (
            <Grid container spacing={3}>
              {/* <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                <Typography variant="h6" className={classes.modelTitle}>Version</Typography>
                <Typography variant="body1" style={{ textAlign: "justify" }} className={classes.modelPara}>{data.version}</Typography>
              </Grid> */}
              <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                <Typography variant="h5" className={classes.modelTitle}>
                  {translate("label.description")}
                </Typography>
                <Typography
                  variant="body1"
                  style={{ textAlign: "justify", marginTop: "15px" }}
                >
                  {data.description}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                <Grid container spacing={2}>
                  {description?.map((des, i) => {
                    if (des.title === "Type" && (task === "asr" || task === "tts")) {
                      return <Grid item xs={4} sm={4} md={4} lg={4} xl={4}>
                        <ModelDescription
                          title={des.title}
                          para={des.para}
                          index={i}
                        />
                      </Grid>
                    } else if (des.title === "Type" && (task !== "asr" || task !== "tts")) {
                      return null
                    } else {
                      return <Grid item xs={4} sm={4} md={4} lg={4} xl={4}>
                        <ModelDescription
                          title={des.title}
                          para={des.para}
                          index={i}
                        />
                      </Grid>
                    }
                  })}
                </Grid>
              </Grid>
              <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                <Box
                  sx={{
                    bgcolor: "background.paper",
                    width: 500,
                  }}
                >
                  <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                    <Typography style={{ marginTop: "3%" }} variant="h5">
                      Benchmarkings
                    </Typography>
                  </Grid>
                  <AppBar
                    color="transparent"
                    style={{ border: "none" }}
                    position="static"
                  >
                    <Tabs
                      value={value}
                      onChange={handleChange}
                      variant="scrollable"
                      scrollButtons={false}
                      aria-label="scrollable prevent tabs example"
                    >
                      {metricArray?.map((metric) => (
                        <Tooltip
                          title={
                            <a
                              style={{ textDecoration: "none" }}
                              href="https://github.com/bhashini-dibd/ulca/wiki/Model-Evaluation-Metrics-Definitions"
                              target="_blank"
                            >{`${metricInfo[metric]}. For further information click here.`}</a>
                          }
                          interactive
                          arrow
                        >
                          <Tab
                            label={metric}
                            onClick={() => handleIndexChange(metric)}
                          />
                        </Tooltip>
                      ))}
                    </Tabs>
                  </AppBar>
                  <TabPanel value={value} index={index}>
                    <BenchmarkTable
                      modelId={params.srno}
                      data={benchmarkPerformance.filter(
                        (benchmark) => benchmark.metric === metricArray[index]
                      )}
                    />
                  </TabPanel>
                </Box>
              </Grid>
            </Grid>
          )}
        </div>
      )}
      {/* <Footer /> */}
      <Contactus />
          <Clients />
          <FooterNewDesign />
    </MuiThemeProvider>
  );
};

export default withStyles(DatasetStyle)(SearchModelDetail);
