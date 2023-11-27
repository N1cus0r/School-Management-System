import useUpdateEntity from "../shared/useUpdateEntity.ts";

const useUpdateCourse = (CACHE_KEY: any[], onSuccess: () => void) =>
  useUpdateEntity("/courses", "Course", CACHE_KEY, onSuccess);

export default useUpdateCourse;
