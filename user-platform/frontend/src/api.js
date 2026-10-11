import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8081/api",
});

// Har request ke saath JWT token bhejo
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export function saveSession({ token, user }) {
  localStorage.setItem("token", token);
  localStorage.setItem("user", JSON.stringify(user));
}

export function clearSession() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
}

export function errorMessage(err) {
  if (err.response?.data?.message) return err.response.data.message;
  if (!err.response) return "Cannot reach the server. Is the backend running on port 8081?";
  return "Something went wrong. Try again.";
}

export default api;
