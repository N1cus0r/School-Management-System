import {useInfiniteQuery} from "react-query";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import {CACHE_KEY_HOMEWORKS} from "../../utils/constants.ts";
import {Homework} from "../../entities/Homework.ts";

const authenticatedApiClient = new AuthenticatedApiClient<Homework>(
  "/homeworks",
);
const useHomeworks = () =>
  useInfiniteQuery({
    queryKey: CACHE_KEY_HOMEWORKS,
    queryFn: ({ pageParam = 0 }) =>
      authenticatedApiClient.getAll({ params: { size: 20, page: pageParam } }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.length > 0 ? allPages.length : undefined;
    },
  });

export default useHomeworks;
