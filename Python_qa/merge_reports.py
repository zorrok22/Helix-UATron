import os
from utils.report_merger import ReportMerger

def main():
    # Define directories
    current_dir = os.path.dirname(os.path.abspath(__file__))
    python_reports_dir = os.path.join(current_dir, 'reports')
    java_reports_dir = os.path.join(current_dir, '..', 'Java_qa', 'Reports')
    output_dir = os.path.join(current_dir, '..', 'combined_reports')
    
    # Create merger instance
    merger = ReportMerger(python_reports_dir, java_reports_dir, output_dir)
    
    # Merge reports
    merged_report_path = merger.merge_reports()
    print(f"Combined report generated at: {merged_report_path}")

if __name__ == '__main__':
    main() 