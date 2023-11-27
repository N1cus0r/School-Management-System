import useDeleteEntity from "../shared/useDeleteEntity.ts";

const useDeleteCourse = (
  CACHE_KEY: any[],
  onDelete: () => void,
  onSuccess: () => void,
  onError: () => void,
) => useDeleteEntity("/courses", CACHE_KEY, onDelete, onSuccess, onError)

export default useDeleteCourse;
