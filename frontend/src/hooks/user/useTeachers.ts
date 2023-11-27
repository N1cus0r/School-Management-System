import { useInfiniteQuery } from "react-query";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import { User } from "../../entities/user/User.ts";
import useUsersStore from "../../store/users-store.ts";
import { CACHE_KEY_TEACHERS } from "../../utils/constants.ts";

const authenticatedApiClient = new AuthenticatedApiClient<User>(
  "/users/teachers",
);

const useTeachers = () => {
  const { teachersSearch } = useUsersStore((s) => s.usersSearch);

  return useInfiniteQuery({
    queryKey: [...CACHE_KEY_TEACHERS, teachersSearch],
    queryFn: ({ pageParam = 0 }) =>
      authenticatedApiClient.getAll({
        params: { size: 20, page: pageParam, fullNameSearch: teachersSearch },
      }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.length > 0 ? allPages.length : undefined;
    },
  });
};

export default useTeachers;
