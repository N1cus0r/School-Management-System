import LocalStorageService from "../services/local-storage-service.ts";
import {Navigate, Outlet} from "react-router-dom";
import {Role} from "../entities/user/Role.ts";
import useDecodedAccessToken from "../hooks/auth/useDecodedAccessToken.ts";
import UnauthorizedPage from "../pages/UnauthorizedPage.tsx";

interface Props {
  allowedRoles: Role[];
}

const PrivateRoute = ({ allowedRoles }: Props) => {
  const token = LocalStorageService.getLocalStorageToken();

  if (!token) return <Navigate to="/login" />;

  const decodedToken = useDecodedAccessToken();

  return allowedRoles.includes(decodedToken.role) ? (
    <Outlet />
  ) : (
    <UnauthorizedPage />
  );
};
export default PrivateRoute;
