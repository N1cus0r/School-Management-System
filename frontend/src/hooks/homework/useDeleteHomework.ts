import useDeleteEntity from "../shared/useDeleteEntity.ts";

const useDeleteHomework = (
  CACHE_KEY: any[],
  onDelete: () => void,
  onSuccess: () => void,
  onError: () => void,
) => useDeleteEntity("/homeworks", CACHE_KEY, onDelete, onSuccess, onError);

export default useDeleteHomework;
