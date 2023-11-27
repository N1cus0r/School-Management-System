import useDeleteEntity from "../shared/useDeleteEntity.ts";

const useDeleteGrade = (
  CACHE_KEY: any[],
  onDelete: () => void,
  onSuccess: () => void,
  onError: () => void,
) => useDeleteEntity("/grades", CACHE_KEY, onDelete, onSuccess, onError);

export default useDeleteGrade
