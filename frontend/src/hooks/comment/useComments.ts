import {useInfiniteQuery} from "react-query";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import {Comment} from "../../entities/Comment.ts";
import {CACHE_KEY_COMMENTS} from "../../utils/constants.ts";

const authenticatedApiClient = new AuthenticatedApiClient<Comment>("/comments");

const useComments = () =>
  useInfiniteQuery({
    queryKey: CACHE_KEY_COMMENTS,
    queryFn: ({ pageParam = 0 }) =>
      authenticatedApiClient.getAll({ params: { size: 20, page: pageParam } }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.length > 0 ? allPages.length : undefined;
    },
  });

export default useComments;
