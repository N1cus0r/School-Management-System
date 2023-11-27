import {useInfiniteQuery} from "react-query";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import {CACHE_KEY_ATTENDANCES} from "../../utils/constants.ts";
import {Attendance} from "../../entities/attendance/Attendance.ts";

const authenticatedApiClient = new AuthenticatedApiClient<Attendance>(
  "/attendances",
);
const useAttendances = () =>
  useInfiniteQuery({
    queryKey: CACHE_KEY_ATTENDANCES,
    queryFn: ({ pageParam = 0 }) =>
      authenticatedApiClient.getAll({ params: { size: 20, page: pageParam } }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.length > 0 ? allPages.length : undefined;
    },
  });

export default useAttendances;
