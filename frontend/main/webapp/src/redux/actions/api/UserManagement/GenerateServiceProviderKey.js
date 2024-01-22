import API from "../../api";
import ENDPOINTS from "../../../../configs/apiendpoints";
import md5 from "md5";

export default class GenerateServiceProviderKeyAPI extends API {
  constructor(ulcaApiKey, serviceProviderName, timeout = 2000) {
    super("POST", timeout, false);
    this.userDetails = JSON.parse(localStorage.getItem("userInfo"));
    this.ulcaApiKey = ulcaApiKey;
    this.serviceProviderName = serviceProviderName;
    this.endpoint = `${super.apiEndPointAuto()}${
      ENDPOINTS.generateServiceProviderKey
    }`;
  }

  apiEndPoint() {
    return this.endpoint;
  }

  getBody() {
    return {
      userID: JSON.parse(localStorage.getItem("userDetails")).userID,
      ulcaApiKey: this.ulcaApiKey,
      serviceProviderName: this.serviceProviderName,
    };
  }

  getHeaders() {
    let res = this.apiEndPoint();
    let urlSha = md5(res);
    let hash = md5(this.userDetails.privateKey + "|" + urlSha);
    this.headers = {
      headers: {
        key: this.userDetails.publicKey,
        sig: hash,
        payload: urlSha,
        "Content-Type": "application/json",
      },
    };

    return this.headers;
  }

  getPayload() {
    return this.credentials;
  }
}
