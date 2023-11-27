import {createContext} from "react";

export interface Credentials {
  email: string;
  password: string;
}

interface AuthContextType {
  login: (credentials: Credentials) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

export default AuthContext

