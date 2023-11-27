import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import {useInfiniteQuery} from "react-query";
import {CACHE_KEY_COURSES} from "../../utils/constants.ts";

const useCourseEntities = <T>(courseId: number | string, endpoint: string) => {
  const authenticatedApiClient = new AuthenticatedApiClient<T>(
    `/courses/${courseId}/${endpoint}`,
  );
  return useInfiniteQuery({
    queryKey: [...CACHE_KEY_COURSES, courseId, endpoint],
    queryFn: ({ pageParam = 0 }) =>
      authenticatedApiClient.getAll({
        params: { size: 20, page: pageParam },
      }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.length > 0 ? allPages.length : undefined;
    },
  });
};

export default useCourseEntities;
