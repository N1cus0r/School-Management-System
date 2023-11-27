import useCreateEntity from "../shared/useCreateEntity.ts";

const useCreateComment = (CACHE_KEY: any[], onSuccess: () => void) =>
    useCreateEntity("/comments", "Comment", CACHE_KEY, onSuccess);

export default useCreateComment
