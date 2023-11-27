import { useQuery } from "react-query";
import { User } from "../../entities/user/User.ts";
import { authenticatedAxiosInstance } from "../../services/authenticated-api-client.ts";

const useProfileImageUrl = (user: User) => {
  return useQuery({
    queryKey: ["user", user.email, "profileImage"],
    queryFn: () => {
      return authenticatedAxiosInstance
        .get(`/users/${user.id}/profile-image`, { responseType: "blob" })
        .then((res) => URL.createObjectURL(res.data));
    },
  });
};

export default useProfileImageUrl;
