import { useQuery } from "react-query";
import ms from "ms";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import { User } from "../../entities/user/User.ts";
import useAuth from "../auth/useAuth.ts";

const authenticatedApiClient = new AuthenticatedApiClient<User>("/users");
const useProfileData = (userEmail: string) => {
  const { logout } = useAuth();
  return useQuery({
    queryKey: ["user", userEmail],
    queryFn: () => authenticatedApiClient.get(userEmail),
    retry: false,
    onError: logout,
    staleTime: ms("1h"),
  });
};

export default useProfileData;
