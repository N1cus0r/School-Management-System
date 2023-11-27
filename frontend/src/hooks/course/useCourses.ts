import {useInfiniteQuery} from "react-query";
import {CACHE_KEY_COURSES} from "../../utils/constants.ts";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import {Course} from "../../entities/Course.ts";
import useCoursesStore from "../../store/courses-store.ts";

const authenticatedApiClient = new AuthenticatedApiClient<Course>("/courses");

const useCourses = () => {
  const coursesSearch = useCoursesStore((s) => s.coursesSearch);
  return useInfiniteQuery({
    queryKey: [...CACHE_KEY_COURSES, coursesSearch],
    queryFn: ({ pageParam = 0 }) =>
      authenticatedApiClient.getAll({
        params: { size: 20, page: pageParam, nameSearch: coursesSearch },
      }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.length > 0 ? allPages.length : undefined;
    },
  });
};

export default useCourses;
