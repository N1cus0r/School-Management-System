import useUsersStore from "../../store/users-store.ts";
import { Role } from "../../entities/user/Role.ts";

const useUsersSearch = (userRole: Role) => {
  const usersStore = useUsersStore();

  return (searchText: string) => {
    if (userRole === Role.TEACHER) usersStore.setTeachersSearch(searchText);
    if (userRole === Role.STUDENT) usersStore.setStudentsSearch(searchText);
  };
};

export default useUsersSearch;
