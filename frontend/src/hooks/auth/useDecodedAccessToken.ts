import { jwtDecode } from "jwt-decode";
import LocalStorageService from "../../services/local-storage-service.ts";
import {Role} from "../../entities/user/Role.ts";

interface DecodedAccessToken {
  role: Role;
  sub: string;
  iat: number;
  exp: number;
}

const useDecodedAccessToken = (): DecodedAccessToken | undefined =>{
    const token = LocalStorageService.getLocalStorageToken()
    return token ? jwtDecode(token) : undefined;
}

export default useDecodedAccessToken;
