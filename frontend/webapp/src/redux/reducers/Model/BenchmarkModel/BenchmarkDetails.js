import { getLanguageName } from "../../../../utils/getLabel";
import C from "../../../actions/constants";

const initialState = {
  benchmarkPerformance: [],
  benchmarkId: "",
  name: "",
  description: "",
  metric: null,
  dataset: "",
  domain: [],
  task: "",
  languages: [],
  createdOn: null,
  submittedOn: null,
};

const addPositions = (data) => {
  return data.map((val, i) => {
    val.position = i + 1;
    return val;
  });
};

const getBenchmarkDetails = (data) => {
  return {
    description: data.description,
    refUrl: data.dataset,
    language:
      data.languages && data.languages[0].targetLanguage !== null
        ? `${getLanguageName(
            data.languages[0].sourceLanguage
          )} - ${getLanguageName(data.languages[0].targetLanguage)}`
        : getLanguageName(data.languages[0].sourceLanguage),
    domain: data.domain ? data.domain.join(", ") : "",
    modelName: data.name,
    metric: data.metric ? data.metric.join(", ") : "",
    task: data.task.type,
    metricArray: data.metric,
    benchmarkPerformance: addPositions(data.benchmarkPerformance),
  };
};

const reducer = (state = initialState, action) => {
  switch (action.type) {
    case C.GET_BENCHMARK_DETAILS:
      const data = getBenchmarkDetails(action.payload);
      return {
        ...data,
      };
    default:
      return {
        ...state,
      };
  }
};

export default reducer;
