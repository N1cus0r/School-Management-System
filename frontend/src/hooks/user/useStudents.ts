import { useInfiniteQuery } from "react-query";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import { User } from "../../entities/user/User.ts";
import { CACHE_KEY_STUDENTS } from "../../utils/constants.ts";
import useUsersStore from "../../store/users-store.ts";

const authenticatedApiClient = new AuthenticatedApiClient<User>(
  "/users/students",
);

const useStudents = () => {
  const { studentsSearch } = useUsersStore((s) => s.usersSearch);
  return useInfiniteQuery({
    queryKey: [...CACHE_KEY_STUDENTS, studentsSearch],
    queryFn: ({ pageParam = 0 }) =>
      authenticatedApiClient.getAll({
        params: { size: 20, page: pageParam, fullNameSearch: studentsSearch },
      }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.length > 0 ? allPages.length : undefined;
    },
  });
};

export default useStudents;
