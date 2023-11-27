import { useParams } from "react-router-dom";
import SkeletonCards from "../components/shared/SkeletonCards.tsx";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import useCourseComments from "../hooks/comment/useCourseComments.ts";
import CommentsCards from "../components/comment/CommentsCards.tsx";
import { Flex, Spinner } from "@chakra-ui/react";
import CreateCommentButton from "../components/comment/CreateCommentButton.tsx";
import React from "react";
import InfiniteScroll from "react-infinite-scroll-component";
import PageUtils from "../utils/page-utils.ts";

const CourseCommentsPage = () => {
  const { courseId } = useParams();

  const {
    data: comments,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useCourseComments(courseId!);

  if (isLoading) return <SkeletonCards />;

  if (!comments || error) return <ErrorMessage />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(comments);

  return (
    <>
      <Flex justifyContent="center">
        <CreateCommentButton />
      </Flex>
      {comments.pages[0].length === 0 ? (
        <ContentPlaceholder />
      ) : (
        <InfiniteScroll
          next={() => fetchNextPage()}
          hasMore={!!hasNextPage}
          loader={<Spinner />}
          dataLength={fetchedDataCount}
        >
          {comments.pages.map((page, index) => (
            <React.Fragment key={index}>
              <CommentsCards comments={page} />
            </React.Fragment>
          ))}
        </InfiniteScroll>
      )}
    </>
  );
};
export default CourseCommentsPage;
