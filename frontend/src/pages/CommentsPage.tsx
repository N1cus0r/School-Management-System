import LoadingSpinner from "../components/shared/LoadingSpinner.tsx";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import useComments from "../hooks/comment/useComments.ts";
import CommentsTable from "../components/comment/CommentsTable.tsx";
import { Spinner } from "@chakra-ui/react";
import InfiniteScroll from "react-infinite-scroll-component";
import PageUtils from "../utils/page-utils.ts";

const CommentsPage = () => {
  const {
    data: comments,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useComments();

  if (isLoading) return <LoadingSpinner />;

  if (!comments || error) return <ErrorMessage />;

  if (comments.pages[0].length === 0) return <ContentPlaceholder />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(comments);

  return (
    <InfiniteScroll
      next={() => fetchNextPage()}
      hasMore={!!hasNextPage}
      loader={<Spinner />}
      dataLength={fetchedDataCount}
    >
      <CommentsTable commentPages={comments.pages} />
    </InfiniteScroll>
  );
};
export default CommentsPage;
