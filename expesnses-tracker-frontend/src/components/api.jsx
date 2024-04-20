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
      if (error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        try {
          const access_token = await refreshAccessToken();
          if (originalRequest.headers) {
            originalRequest.headers['Authorization'] = `Bearer ${access_token}`;
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
            window.location.href = '/login';
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
      const response = await axios.post('http://localhost:8000/api/users/refreshToken', localStorage.getItem('access_token'));

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
    }
  }
  export default api;
