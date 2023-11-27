import { useParams } from "react-router-dom";
import SkeletonCards from "../components/shared/SkeletonCards.tsx";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import useCourseGrades from "../hooks/grade/useCourseGrades.ts";
import GradesCards from "../components/grade/GradesCards.tsx";
import { Flex, Spinner } from "@chakra-ui/react";
import CreateGradeButton from "../components/grade/CreateGradeButton.tsx";
import PageUtils from "../utils/page-utils.ts";
import React from "react";
import InfiniteScroll from "react-infinite-scroll-component";

const CourseGradesPage = () => {
  const { courseId } = useParams();

  const {
    data: grades,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useCourseGrades(courseId!);

  if (isLoading) return <SkeletonCards />;

  if (!grades || error) return <ErrorMessage />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(grades);

  return (
    <>
      <Flex justifyContent="center">
        <CreateGradeButton />
      </Flex>
      {grades.pages[0].length === 0 ? (
        <ContentPlaceholder />
      ) : (
        <InfiniteScroll
          next={() => fetchNextPage()}
          hasMore={!!hasNextPage}
          loader={<Spinner />}
          dataLength={fetchedDataCount}
        >
          {grades.pages.map((page, index) => (
            <React.Fragment key={index}>
              <GradesCards grades={page} />
            </React.Fragment>
          ))}
        </InfiniteScroll>
      )}
    </>
  );
};
export default CourseGradesPage;
