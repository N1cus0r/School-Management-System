import useUpdateEntity from "../shared/useUpdateEntity.ts";

const useUpdateComment = (CACHE_KEY: any[], onSuccess: () => void) =>
  useUpdateEntity("/comments", "Comment", CACHE_KEY, onSuccess);

export default useUpdateComment;
