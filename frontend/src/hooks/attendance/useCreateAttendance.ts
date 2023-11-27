import useCreateEntity from "../shared/useCreateEntity.ts";

const useCreateAttendance = (CACHE_KEY: any[], onSuccess: () => void) =>
  useCreateEntity("/attendances", "Attendance", CACHE_KEY, onSuccess);

export default useCreateAttendance;
