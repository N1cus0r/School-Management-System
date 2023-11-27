import useCoursesStore from "../../store/courses-store.ts";

const useCoursesSearch = () => {
  return useCoursesStore((s) => s.setCoursesSearch);
};

export default useCoursesSearch;
