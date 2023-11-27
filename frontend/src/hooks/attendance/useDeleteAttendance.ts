import useDeleteEntity from "../shared/useDeleteEntity.ts";

const useDeleteAttendance = (
  CACHE_KEY: any[],
  onDelete: () => void,
  onSuccess: () => void,
  onError: () => void,
) => useDeleteEntity("/attendances", CACHE_KEY, onDelete, onSuccess, onError);

export default useDeleteAttendance;
