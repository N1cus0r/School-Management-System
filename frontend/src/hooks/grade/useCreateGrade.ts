import useCreateEntity from "../shared/useCreateEntity.ts";

const useCreateGrade = (CACHE_KEY: any[], onSuccess: () => void) =>
  useCreateEntity("/grades", "Grade", CACHE_KEY, onSuccess);


export default useCreateGrade
