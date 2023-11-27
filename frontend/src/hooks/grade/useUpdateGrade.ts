import useUpdateEntity from "../shared/useUpdateEntity.ts";

const useUpdateGrade = (CACHE_KEY: any[], onSuccess: () => void) =>
  useUpdateEntity("/grades", "Grade", CACHE_KEY, onSuccess);

export default useUpdateGrade

