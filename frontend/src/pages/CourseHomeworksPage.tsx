import useCourseHomeworks from "../hooks/homework/useCourseHomeworks.ts";
import { useParams } from "react-router-dom";
import SkeletonCards from "../components/shared/SkeletonCards.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import HomeworksCards from "../components/homework/HomeworksCards.tsx";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import { Flex, Spinner } from "@chakra-ui/react";
import CreateHomeworkButton from "../components/homework/CreateHomeworkButton.tsx";
import InfiniteScroll from "react-infinite-scroll-component";
import React from "react";
import PageUtils from "../utils/page-utils.ts";

const CourseHomeworksPage = () => {
  const { courseId } = useParams();

  const {
    data: homeworks,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useCourseHomeworks(courseId!);

  if (isLoading) return <SkeletonCards />;

  if (!homeworks || error) return <ErrorMessage />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(homeworks);

  return (
    <>
      <Flex justifyContent="center">
        <CreateHomeworkButton />
      </Flex>
      {homeworks.pages[0].length === 0 ? (
        <ContentPlaceholder />
      ) : (
        <InfiniteScroll
          next={() => fetchNextPage()}
          hasMore={!!hasNextPage}
          loader={<Spinner />}
          dataLength={fetchedDataCount}
        >
          {homeworks.pages.map((page, index) => (
            <React.Fragment key={index}>
              <HomeworksCards homeworks={page} />
            </React.Fragment>
          ))}
        </InfiniteScroll>
      )}
    </>
  );
};
export default CourseHomeworksPage;
