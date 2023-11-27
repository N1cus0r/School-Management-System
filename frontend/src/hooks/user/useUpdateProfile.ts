import useUpdateEntity from "../shared/useUpdateEntity.ts";

const useUpdateProfile = (CACHE_KEY: any[], onSuccess: () => void) =>
  useUpdateEntity("/users", "Profile", CACHE_KEY, onSuccess);

export default useUpdateProfile;
