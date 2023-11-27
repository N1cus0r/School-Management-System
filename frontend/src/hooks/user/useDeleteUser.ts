import useDeleteEntity from "../shared/useDeleteEntity.ts";

const useDeleteUser = (
  CACHE_KEY: any[],
  onDelete: () => void,
  onSuccess: () => void,
  onError: () => void,
) => useDeleteEntity("/users", CACHE_KEY, onDelete, onSuccess, onError);

export default useDeleteUser;
