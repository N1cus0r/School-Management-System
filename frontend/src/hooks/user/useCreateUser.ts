import useCreateEntity from "../shared/useCreateEntity.ts";

const useCreateUser = (CACHE_KEY: any[], onSuccess: () => void) =>
  useCreateEntity("/users/register", "User", CACHE_KEY, onSuccess);

export default useCreateUser;
