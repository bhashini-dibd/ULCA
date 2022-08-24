const endpoints = {
  getContributionList: "/ulca/apis/v0/dataset/listByUserId",
  getBenchmarkList: "/ulca/apis/v0/benchmark/listByUserId",
  getDetailReport: "/ulca/apis/v0/dataset/getByServiceRequestNumber",
  dataSetSearchApi: "/ulca/data-metric/v0/store/search",
  login: "/ulca/user-mgmt/v1/users/login",
  datasetSubmit: "/ulca/apis/v0/dataset/corpus/submit",
  getSearchOptions: "/ulca/mdms/v0/fetch-master/bulk",
  datasetBenchmarkSubmit: "/ulca/apis/v0/benchmark/submit",
  mySearches: "/ulca/apis/v0/dataset/corpus/search/listByUserId",
  submitSearchReq: "/ulca/apis/v0/dataset/corpus/search",
  errorReport: "/ulca/error-consumer/v0/error/report",
  register: "/ulca/user-mgmt/v1/users/signup",
  activateUser: "/ulca/user-mgmt/v1/users/verify-user",
  forgotPassword: "/ulca/user-mgmt/v1/users/forgot-password",
  tokenSearch: "/ulca/user-mgmt/v1/users/get/token/status",
  resetPassword: "/ulca/user-mgmt/v1/users/reset-password",
  modelSubmit: "/ulca/apis/v0/model/upload",
  getModelContributionList: "/ulca/apis/v0/model/listByUserId",
  modelSearch: "/ulca/apis/v0/model/search",
  hostedInference: "/ulca/apis/v0/model/compute",
  hostedVoice: "/ulca/apis/asr/v1/model/compute",
  getBenchmarkDetails: "/ulca/apis/v0/benchmark/getByTask",
  submitBenchmark: "/ulca/apis/v0/benchmark/execute/allMetric",
  benchmarkTable: "/ulca/apis/v0/model/getModel",
  benchmarkModelSearch: "/ulca/apis/v0/benchmark/search",
  benchmarkDetails: "/ulca/apis/v0/benchmark/getBenchmark",
  toggleModelStatus: "/ulca/apis/v0/model/status/change",
  getUserDetails: "/ulca/user-mgmt/v1/users/search",
  updateUserDetails: "/ulca/user-mgmt/v1/users/update",
  updateUserStatus: "/ulca/user-mgmt/v1/users/update/active/status",
  getMasterData: "/ulca/mdms/v0/fetch-master/bulk",
  getModel: "/ulca/apis/v0/model/getModel",
  ocrDocumentUpload: "/ulca/apis/v0/model/tryMe",
  submitFeedback:"/ulca/apis/v0/model/feedback/submit",
  datasetMetrics: "/ulca/data-metric/v0/store/reportdata",
  getModelHealthStatus: "/ulca/apis/v0/model/getModelHealthStatus",
};

export default endpoints;
