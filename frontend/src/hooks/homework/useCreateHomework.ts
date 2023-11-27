import useCreateEntity from "../shared/useCreateEntity.ts";

const useCreateHomework = (CACHE_KEY: any[], onSuccess: () => void) =>
  useCreateEntity("/homeworks", "Homework", CACHE_KEY, onSuccess);

export default useCreateHomework;
