import { useContext } from "react";
import AuthContext from "../../context/authContext.tsx";

const useAuth = () => useContext(AuthContext);

export default useAuth;
