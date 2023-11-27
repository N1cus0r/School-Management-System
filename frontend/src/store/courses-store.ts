import { create } from "zustand";

interface CoursesStore {
  coursesSearch?: string;
  setCoursesSearch: (searchText: string) => void;
}

const useCoursesStore = create<CoursesStore>((set) => ({
  coursesSearch: "",
  setCoursesSearch: (searchText) =>
    set({
      coursesSearch: searchText,
    }),
}));

export default useCoursesStore;
