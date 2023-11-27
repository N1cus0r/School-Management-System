import axios, { AxiosRequestConfig } from "axios";
import LocalStorageService from "./local-storage-service.ts";

export const authenticatedAxiosInstance = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api/v1`,
});

authenticatedAxiosInstance.interceptors.request.use(
  (config) => {
    if (!config.headers.Authorization) {
      config.headers.Authorization = `Bearer ${LocalStorageService.getLocalStorageToken()}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

class AuthenticatedApiClient<T> {
  endpoint: string;

  constructor(endpoint: string) {
    this.endpoint = endpoint;
  }

  getAll = (config?: AxiosRequestConfig) => {
    return authenticatedAxiosInstance
      .get<T[]>(this.endpoint, config)
      .then((res) => res.data);
  };

  get = (id: number | string) => {
    return authenticatedAxiosInstance
      .get<T>(this.endpoint + "/" + id)
      .then((res) => res.data);
  };

  post = (data: T) => {
    return authenticatedAxiosInstance
      .post(this.endpoint, data)
      .then((res) => res.data);
  };

  put = (id: number, data: T) => {
    return authenticatedAxiosInstance
      .put(this.endpoint + "/" + id, data)
      .then((res) => res.data);
  };
  delete = (id: number) => {
    return authenticatedAxiosInstance
      .delete(this.endpoint + "/" + id)
      .then((res) => res.data);
  };
}

export default AuthenticatedApiClient;
