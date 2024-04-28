import axios from "axios";
import myconfig  from "./config";
import DeviceIdHolder from "./basics/UserContextProvider/deviceIdHolder";

const api = axios.create({
    baseURL: myconfig.baseURL
  });
  
  api.interceptors.request.use(
    async config => {
      const token = localStorage.getItem('access_token');
      if (token) {
        config.headers = {
          Authorization: `Bearer ${token}`,
          Accept: 'application/json',
          'Content-Type': 'application/json',
          'Device-ID': DeviceIdHolder.getDeviceId(),
        }
      }
      return config;
    },
    error => {
      Promise.reject(error)
  });
  

  api.interceptors.response.use(
    (response) => {
      return response;
    },
    async function (error) {
      const originalRequest = error.config;
      if (!originalRequest._retryCount) {
        originalRequest._retryCount = 0;
      }
      if (error.response.status === 401 && originalRequest._retryCount < 3) {
        originalRequest._retryCount += 1;
        try {
          const access_token = await refreshAccessToken();
          if (originalRequest.headers) {
            originalRequest.headers['Authorization'] = `Bearer ${access_token}`;
            originalRequest.headers['Device-ID'] = `${DeviceIdHolder.getDeviceId()}`;
          } else {
            originalRequest.headers = {
              'Authorization': `Bearer ${access_token}`,
              Accept: 'application/json',
              'Content-Type': 'application/json',
              'Device-ID': DeviceIdHolder.getDeviceId(),
            };
          }
          const response = await axios(originalRequest);
          return response;
        } catch (retryError) {
          console.error('Error during retry:', retryError);
          if (retryError?.response?.status === 401){
            localStorage.removeItem('access_token');
            localStorage.removeItem('refresh_token');    
            localStorage.removeItem('deviceId');    
            // window.location.href = '/login';
          }
          return Promise.reject(retryError);
        }
      }
      return Promise.reject(error);
    }
  );
  
  async function refreshAccessToken() {
    try {
      const refresh_token = localStorage.getItem('refresh_token');
      axios.defaults.headers.common['Authorization'] = 'Bearer ' + refresh_token;
      axios.defaults.headers.common['Accept'] = 'application/json';
      axios.defaults.headers.common['Content-Type'] = 'application/json';
      axios.defaults.headers.common['Device-ID'] = DeviceIdHolder.getDeviceId();
      const response = await axios.post('http://localhost:8080/expenses-tracker/api/users/refreshToken', localStorage.getItem('access_token'));

      if (response.status === 200) {
        localStorage.setItem('access_token', response.data.data.accessToken);
        
        return response.data.data.accessToken;
      } else {
        throw new Error('Unable to refresh token. Status: ' + response.status);
      }
    } catch (error) {
      console.error('Error refreshing access token:', error.message);
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      localStorage.removeItem("deviceId");  
    }
  }
  export default api;
