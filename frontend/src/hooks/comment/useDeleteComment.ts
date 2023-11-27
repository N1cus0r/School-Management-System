import useDeleteEntity from "../shared/useDeleteEntity.ts";

const useDeleteComment = (
    CACHE_KEY: any[],
    onDelete: () => void,
    onSuccess: () => void,
    onError: () => void,
) => useDeleteEntity("/comments", CACHE_KEY, onDelete, onSuccess, onError);

export default useDeleteComment
