import useUpdateEntity from "../shared/useUpdateEntity.ts";

const useUpdateUser = (CACHE_KEY: any[], onSuccess: () => void) =>
  useUpdateEntity("/users", "User", CACHE_KEY, onSuccess);

export default useUpdateUser;
