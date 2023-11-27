import { create } from "zustand";

interface UsersSearch {
  teachersSearch?: string;
  studentsSearch?: string;
}

interface UsersStore {
  usersSearch: UsersSearch;
  setTeachersSearch: (searchText: string) => void;
  setStudentsSearch: (searchText: string) => void;
}
const useUsersStore = create<UsersStore>((set) => ({
  usersSearch: { teachersSearch: "", studentsSearch: "" },
  setTeachersSearch: (searchText) =>
    set((store) => ({
      usersSearch: { ...store.usersSearch, teachersSearch: searchText },
    })),
  setStudentsSearch: (searchText) =>
    set((store) => ({
      usersSearch: { ...store.usersSearch, studentsSearch: searchText },
    })),
}));

export default useUsersStore;
