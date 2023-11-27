import useCreateEntity from "../shared/useCreateEntity.ts";

const useCreateCourse = (CACHE_KEY: any[], onSuccess: () => void) =>
    useCreateEntity("/courses", "Course", CACHE_KEY, onSuccess)

export default useCreateCourse;
