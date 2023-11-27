import useUpdateEntity from "../shared/useUpdateEntity.ts";

const useUpdateHomework = (CACHE_KEY: any[], onSuccess: () => void) =>
  useUpdateEntity("/homeworks", "Homework", CACHE_KEY, onSuccess);

export default useUpdateHomework;
