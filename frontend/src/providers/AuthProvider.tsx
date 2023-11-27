import { ReactNode } from "react";
import AuthContext, { Credentials } from "../context/authContext.tsx";
import NotificationService from "../services/notification-service.ts";
import LocalStorageService from "../services/local-storage-service.ts";
import { useNavigate } from "react-router-dom";
import apiClient from "../services/api-client.ts";
import { jwtDecode } from "jwt-decode";
import { Role } from "../entities/user/Role.ts";

interface Props {
  children: ReactNode;
}
const AuthProvider = ({ children }: Props) => {
  const navigate = useNavigate();

  const navigateBasedOnRole = (role: Role) => {
    switch (role) {
      case Role.ADMIN:
        navigate("/teachers");
        break;
      case Role.TEACHER:
        navigate("/students");
        break;
      case Role.STUDENT:
        navigate("/grades");
        break;
    }
  };

  const login = async (credentials: Credentials) => {
    apiClient
      .post("/auth/login", credentials)
      .then((res) => {
        LocalStorageService.setLocalStorageToken(res.data.token);
        const { role } = jwtDecode<{ role: Role }>(res.data.token);
        navigateBasedOnRole(role);
      })
      .catch((err) => {
        NotificationService.errorNotification(
          "Login",
          err.response.data.message,
        );
      });
  };

  const logout = () => {
    LocalStorageService.delLocalStorageToken();
    navigate("/login");
  };

  return (
    <AuthContext.Provider value={{ login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
export default AuthProvider;
