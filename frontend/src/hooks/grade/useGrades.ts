import {useQuery} from "react-query";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import {CACHE_KEY_GRADES} from "../../utils/constants.ts";
import {Grade} from "../../entities/Grade.ts";

const authenticatedApiClient = new AuthenticatedApiClient<Grade>("/grades")
const useGrades = () =>
  useQuery({
    queryKey: CACHE_KEY_GRADES,
    queryFn: () =>
        authenticatedApiClient
            .getAll({params: {size:150}})
  });

export default useGrades;
