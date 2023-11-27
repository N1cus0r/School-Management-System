import useUpdateEntity from "../shared/useUpdateEntity.ts";

const useUpdateAttendance = (CACHE_KEY: any[], onSuccess: () => void) =>
  useUpdateEntity("/attendances", "Attendance", CACHE_KEY, onSuccess);

export default useUpdateAttendance

